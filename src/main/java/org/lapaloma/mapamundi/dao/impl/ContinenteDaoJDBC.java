package org.lapaloma.mapamundi.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.lapaloma.mapamundi.dao.IContinenteDAO;
import org.lapaloma.mapamundi.vo.Continente;
import org.springframework.stereotype.Repository;

@Repository
public class ContinenteDaoJDBC implements IContinenteDAO {
	private final DataSource dataSource;

	// Spring inyecta el DataSource configurado automáticamente
	public ContinenteDaoJDBC(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Continente obtenerContinentePorClave(String codigo) {
		Continente continente = null;

		String sentenciaSQL = """
				SELECT * FROM T_CONTINENTE
				WHERE codigo=?
				""";

		try (Connection conexion = dataSource.getConnection();
				PreparedStatement sentenciaJDBCPreparada = conexion.prepareStatement(sentenciaSQL);) {

			sentenciaJDBCPreparada.setString(1, codigo);
			System.out.println(sentenciaJDBCPreparada);

			ResultSet resultadoSentencia = sentenciaJDBCPreparada.executeQuery();

			if (resultadoSentencia.next()) {
				continente = getLineaFromResultSet(resultadoSentencia);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return continente;
	}

	@Override
	public void actualizarContinente(Continente continente) {

		String sentenciaSQL = """
				UPDATE T_CONTINENTE
				SET nombre_continente=?
				WHERE codigo=?
				""";

		try (Connection conexion = dataSource.getConnection();
				PreparedStatement sentenciaJDBCPreparada = conexion.prepareStatement(sentenciaSQL);) {

			sentenciaJDBCPreparada.setString(1, continente.getNombre());
			sentenciaJDBCPreparada.setString(2, continente.getCodigo());

			System.out.println(sentenciaJDBCPreparada);

			sentenciaJDBCPreparada.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void crearContinente(Continente continente) {

		String sentenciaSQL = """
				INSERT INTO T_CONTINENTE (codigo, nombre_continente)
				VALUES (?, ?)
				""";

		try (Connection conexion = dataSource.getConnection();
				PreparedStatement sentenciaJDBCPreparada = conexion.prepareStatement(sentenciaSQL);) {

			sentenciaJDBCPreparada.setString(1, continente.getCodigo());
			sentenciaJDBCPreparada.setString(2, continente.getNombre());

			System.out.println(sentenciaJDBCPreparada);

			sentenciaJDBCPreparada.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void borrarContinente(Continente continente) {

		String sentenciaSQL = """
				DELETE FROM T_CONTINENTE
				WHERE codigo=?
				""";

		try (Connection conexion = dataSource.getConnection();
				PreparedStatement sentenciaJDBCPreparada = conexion.prepareStatement(sentenciaSQL);) {

			sentenciaJDBCPreparada.setString(1, continente.getCodigo());

			sentenciaJDBCPreparada.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Continente> obtenerListaContinentes() {

		List<Continente> lista = new ArrayList<>();

		String sentenciaSQL = """
				SELECT * FROM T_CONTINENTE
				""";

		try (Connection conexion = dataSource.getConnection();
				PreparedStatement sentenciaJDBCPreparada = conexion.prepareStatement(sentenciaSQL);) {

			System.out.println(sentenciaJDBCPreparada);

			ResultSet resultadoSentencia = sentenciaJDBCPreparada.executeQuery();

			while (resultadoSentencia.next()) {
				lista.add(getLineaFromResultSet(resultadoSentencia));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

	@Override
	public List<Continente> obtenerContinentePorNombre(String nombre) {

		List<Continente> lista = new ArrayList<>();

		String sentenciaSQL = """
				SELECT * FROM T_CONTINENTE
				WHERE nombre_continente LIKE ?
				""";

		try (Connection conexion = dataSource.getConnection();
				PreparedStatement sentenciaJDBCPreparada = conexion.prepareStatement(sentenciaSQL);) {

			sentenciaJDBCPreparada.setString(1, "%" + nombre + "%");

			System.out.println(sentenciaJDBCPreparada);

			ResultSet resultadoSentencia = sentenciaJDBCPreparada.executeQuery();

			while (resultadoSentencia.next()) {
				lista.add(getLineaFromResultSet(resultadoSentencia));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

	private Continente getLineaFromResultSet(ResultSet resultadoSentencia) throws SQLException {

		Continente continente = new Continente();

		continente.setCodigo(resultadoSentencia.getString("codigo"));
		continente.setNombre(resultadoSentencia.getString("nombre_continente"));

		return continente;
	}
}